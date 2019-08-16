package com.sdt.nepush.net;


import com.sdt.nepush.bean.LoginRestResp;
import com.sdt.nepush.bean.RegisterResp;
import com.sdt.nepush.bean.UserBean;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @desc
 * @created
 * @createdDate 2019/2/13 11:07
 * @updated
 * @updatedDate 2019/2/13 11:07
 **/

public interface MedicalService {

    @POST("/user/register")
    Observable<RegisterResp> register(@Body UserBean user);

    @GET("/user/login")
    Observable<LoginRestResp> login(@Query("username") String name, @Query("password") String password);


}
