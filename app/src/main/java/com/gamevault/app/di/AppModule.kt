package com.gamevault.app.di

import android.content.Context
import androidx.room.Room
import com.gamevault.app.R
import com.gamevault.app.data.local.GameVaultDatabase
import com.gamevault.app.data.local.dao.CollectionDao
import com.gamevault.app.data.local.dao.GameCacheDao
import com.gamevault.app.data.remote.GameVaultApi
import com.gamevault.app.data.remote.RawgApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    @Named("rawg")
    fun provideRawgRetrofit(okHttp: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideRawgApi(@Named("rawg") retrofit: Retrofit): RawgApi =
        retrofit.create(RawgApi::class.java)

    @Provides
    @Singleton
    @Named("custom")
    fun provideCustomRetrofit(okHttp: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(com.gamevault.app.BuildConfig.CUSTOM_API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideGameVaultApi(@Named("custom") retrofit: Retrofit): GameVaultApi =
        retrofit.create(GameVaultApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GameVaultDatabase =
        Room.databaseBuilder(context, GameVaultDatabase::class.java, GameVaultDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCollectionDao(db: GameVaultDatabase): CollectionDao = db.collectionDao()

    @Provides
    @Singleton
    fun provideGameCacheDao(db: GameVaultDatabase): GameCacheDao = db.gameCacheDao()

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }
}
