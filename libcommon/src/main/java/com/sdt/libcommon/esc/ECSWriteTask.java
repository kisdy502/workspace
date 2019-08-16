package com.sdt.libcommon.esc;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;


/**
 * Created by Administrator on 2018/8/21.
 */

public class ECSWriteTask {

    public static final String LOG_FILE_NAME = "ECS.txt";

    private final static long MAX_SIZE = 1024 * 1024 * 3L;   //file max length:3MB

    private String filePath;
    private long max_size = MAX_SIZE;               //file max length:3MB
    boolean createFile = true;

    public ECSWriteTask(String logDir) {
        this(logDir, MAX_SIZE);
    }

    public ECSWriteTask(String logDir, long max_size) {
        this.filePath = logDir;
        this.max_size = max_size;
        pool.execute(new WriteThread());
    }

    private LinkedBlockingQueue<LogBean> printQueue = new LinkedBlockingQueue<LogBean>(Integer.MAX_VALUE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });


    public synchronized void addTask(int level, String tag, String msg) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            for (int i = 0, len = stackTraceElements.length; i < len; ++i) {
                if (i == 5) {
                    StackTraceElement t = stackTraceElements[i];
                    Date date = new Date();
                    LogBean bean = new LogBean();
                    bean.timeStr = sdf.format(date);
                    bean.level = level;
                    bean.tag = tag;
                    bean.className = t.getClassName();
                    bean.msg = msg;
                    bean.func = t.getMethodName();
                    bean.line = t.getLineNumber();
                    printQueue.add(bean);
                }
            }
        }
    }


    private void write(String sb) {
        File file = checkFile(filePath);
        if (file != null) {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file, "rw");
                raf.seek(file.length());
                raf.write(sb.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(raf);
            }
        }
    }

    private File checkFile(String filePath) {
        if (!TextUtils.isEmpty(filePath) && createFile) {
            String fileName = filePath.endsWith("/") ? filePath + LOG_FILE_NAME : filePath + "/" + LOG_FILE_NAME;
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    boolean result = file.createNewFile();
                    if (!result) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    createFile = false;
                }
            }

            long size = file.length();
            if (size > this.max_size) {
                file = createCpyFile(file, true);        //文件满了
            }
            return file.exists() ? file : null;
        }
        return null;

    }

    public File createCpyFile(File oldFile, boolean deleteOld) {
        if (oldFile == null) {
            return null;
        } else {
            File newFile = null;
            newFile = new File(oldFile.getAbsolutePath() + ".bak");
            if (newFile.exists()) {
                newFile.delete();
            }
            copyFile(oldFile, newFile);
            if (deleteOld) {
                oldFile.delete();
            }
            return oldFile;
        }
    }

    public void copyFile(File oldfile, File newfile) {
        if (oldfile != null && newfile != null) {
            InputStream is = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            String line = "";

            try {
                is = new FileInputStream(oldfile);
                br = new BufferedReader(new InputStreamReader(is));
                bw = new BufferedWriter(new FileWriter(newfile));

                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.write("\n");
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(is);
                IOUtils.close(br);
                IOUtils.close(bw);
            }
        }
    }

    private class WriteThread implements Runnable {

        private WriteThread() {
            Log.d("WriteThread", "write log at work thread");
        }

        public void run() {
            try {
                while (true) {
                    LogBean logBean = printQueue.take();
                    write(logBean.toString());
                    Thread.yield();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
