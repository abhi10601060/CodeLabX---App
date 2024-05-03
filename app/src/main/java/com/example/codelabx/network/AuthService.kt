package com.example.codelabx.network

import com.example.codelabx.models.AuthResponse
import com.example.codelabx.models.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @POST("/signup")
    @Headers( "Accept: application/json", "Content-Type: application/json")
    suspend fun signUp(@Body userDetails : UserDetails) : Response<AuthResponse>

    @POST("/login")
    @Headers( "Accept: application/json", "Content-Type: application/json")
    suspend fun login(@Body userDetails : UserDetails) : Response<AuthResponse>

}