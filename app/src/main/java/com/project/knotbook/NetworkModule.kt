package com.project.knotbook

import com.project.knotbook.api.AuthApi
import com.project.knotbook.api.DashboardApi
import com.project.knotbook.api.ProjectApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * creates a singleton object. There is only one instance.
 * A Hilt module doesn't need to be instantiated to provide dependencies.
 * Its functions describe how to construct them.
 */

@Module //tells hilt the instructions for creating dependencies that you need
@InstallIn(SingletonComponent::class) //This tells Hilt which component should contain these dependencies.
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides //When someone requests an OkHttpClient,call this function to create one"
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient{

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, //We don't manually call provideOkHttpClient().Hilt sees Retrofit requires OkHttpClient.
                                 // It finds the matching provider function and supplies the dependency automatically.
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)


    @Provides
    @Singleton
    fun provideDashboardApi(retrofit: Retrofit): DashboardApi =
        retrofit.create(DashboardApi::class.java)

    @Provides
    @Singleton
    fun provideProjectApi(retrofit: Retrofit): ProjectApi =
        retrofit.create(ProjectApi::class.java)
}