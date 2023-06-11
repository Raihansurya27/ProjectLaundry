package com.raihan.projectlaundry.api

import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ItemModel
import com.raihan.projectlaundry.model.LayoutModel
import com.raihan.projectlaundry.model.LoginModel
import com.raihan.projectlaundry.model.OrderDetailModel
import com.raihan.projectlaundry.model.OrderModel
import com.raihan.projectlaundry.model.SalesToday
import com.raihan.projectlaundry.model.ServiceModel
import com.raihan.projectlaundry.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

    //Login dan register
    @POST("/login.php")
    @FormUrlEncoded
    suspend fun login(
        @Field("phone_number") phoneNumber: String,
        @Field("password") password: String
    ): Response<LoginModel>

    @Multipart
    @POST("/register.php")
    fun register(
        @Part("username") username: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address") address: RequestBody,
        @Part("role") role: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>


    //user order
    //belum dipasang
    @GET("/getuserorder.php")
    fun getUserOrder(@Query("order_id") order_id: String): Call<List<OrderModel>>

    @GET("/getuserorderdetail.php")
    fun getUserOrderDetail(@Query("order_id") order_id: String): Call<List<OrderModel>>

    @POST("/insertorder.php")
    @FormUrlEncoded
    fun insertOrder(
        @Field("order_id") order_id: String,
        @Field("phone_number") phone_number: String,
        @Field("service_id") service_id: String,
        @Field("status") status: String,
        @Field("total_price") total_price: Int,
        @Field("total_weight") total_weight: Int,
    ): Call<ApiResponse>

    @POST("/insertorderdetail.php")
    @FormUrlEncoded
    fun insertOrderDetail(
        @Field("order_id") service_id: String,
        @Field("item_id") item_id: String,
        @Field("quantity") quantity: Int,
    ): Call<ApiResponse>

    @GET("/deleteorder.php")
    fun deleteOrder(@Query("order_id") order_id: String): Call<ApiResponse>

    @GET("/deleteorderdetail.php")
    fun deleteOrderDetail(@Query("order_id") order_id: String): Call<ApiResponse>



    //Universal
    @GET("/getuser.php")
    fun getUser(@Query("phone_number") phone_number: String): Call<List<UserModel>>

    //AccountActivity
    @GET("/searchuser.php")
    fun searchUser(@Query("keyword") keyword: String): Call<List<UserModel>>

    @GET("/getallusers.php")
    fun getAllUser(): Call<List<UserModel>>

    @GET("/deleteuser.php")
    fun deleteUser(@Query("phone_number") phoneNumber: String): Call<ApiResponse>

    @Multipart
    @POST("/insertuser.php")
    fun insertUser(
        @Part("username") username: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address") address: RequestBody,
        @Part("role") role: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateuser1.php")
    fun updateUser1(
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address") address: RequestBody,
        @Part("role") role: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateuser2.php")
    fun updateUser2(
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("address") address: RequestBody,
        @Part("role") role: RequestBody,
    ): Call<ApiResponse>


    //ItemActivity
    @GET("/getitem.php")
    fun getItem(@Query("item_id") itemId: String): Call<List<ItemModel>>

    @GET("/searchitem.php")
    fun searchItem(@Query("keyword") keyword: String): Call<List<ItemModel>>

    @GET("/getallitems.php")
    fun getAllItem(): Call<List<ItemModel>>

    @GET("/deleteitem.php")
    fun deleteItem(@Query("item_id") itemId: String): Call<ApiResponse>

    @Multipart
    @POST("/insertitem.php")
    fun insertItem(
        @Part("item_id") item_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateitem1.php")
    fun updateItem1(
        @Part("item_id") itemId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateitem2.php")
    fun updateItem2(
        @Part("item_id") itemId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("desc") desc: RequestBody,
    ): Call<ApiResponse>


    //Service Activity
    @GET("/getservice.php")
    fun getService(@Query("service_id") serviceId: String): Call<List<ServiceModel>>

    @GET("/searchservice.php")
    fun searchService(@Query("keyword") keyword: String): Call<List<ServiceModel>>

    @GET("/getallservices.php")
    fun getAllService(): Call<List<ServiceModel>>

    @GET("/deleteservice.php")
    fun deleteService(@Query("service_id") service_id: String): Call<ApiResponse>

    @Multipart
    @POST("/insertservice.php")
    fun insertService(
        @Part("service_id") service_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("per_kg") per_kg: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateservice1.php")
    fun updateService1(
        @Part("service_id") service_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("per_kg") per_kg: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>

    @Multipart
    @POST("/updateservice2.php")
    fun updateService2(
        @Part("service_id") service_id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("per_kg") per_kg: RequestBody,
    ): Call<ApiResponse>

    //Calculation Activity
    @GET("/getallitemservice.php")
    fun getAllItemsService(@Query("service_id") service_id: String): Call<List<ItemModel>>


    //History Activity
    @GET("/getallorder.php")
    fun getAllOrder(): Call<List<OrderModel>>

    @GET("/getorderdetail.php")
    fun getOrderDetail(): Call<List<OrderDetailModel>>

//    @Multipart
//    @POST("/updateservice2.php")
//    fun updateService2(
//        @Part("service_id") service_id: RequestBody,
//        @Part("name") name: RequestBody,
//        @Part("desc") desc: RequestBody,
//        @Part("price") price: RequestBody,
//        @Part("per_kg") per_kg: RequestBody,
//    ): Call<ApiResponse>


    //Layout Activity
    @GET("/getalllayouts.php")
    fun getAllLayout(): Call<List<LayoutModel>>

    @POST("/layoutservice.php")
    @FormUrlEncoded
    fun layoutService(
        @Field("service_id") service_id: String,
        @Field("item_id") item_id: String,
    ): Call<ApiResponse>

    @GET("/layoutdelete.php")
    fun deleteLayout(@Query("service_id") service_id: String): Call<ApiResponse>

    //Admin
    @GET("/gettodaysale.php")
    fun getTodaySale(): Call<List<SalesToday>>
}