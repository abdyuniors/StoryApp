package com.example.storyapp.data.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.db.StoryDatabase
import com.example.storyapp.data.response.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    companion object {
        private const val TOKEN = "Bearer TOKEN"
    }

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            TOKEN
        )
        val pagingState = PagingState<Int, ListStory>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override fun login(email: String, password: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun register(name: String, email: String, password: String): Call<RegisterResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoriesByPage(token: String, page: Int, size: Int): StoryResponse {
        val listStory = mutableListOf<ListStory>()
        for (i in 1..10) {
            listStory.add(
                ListStory(
                    id = "id-$i",
                    name = "name $i",
                    photoUrl = "https://example.com/photo_$i.jpg",
                    description = "This is the description for Story $i",
                    createdAt = "2023-03-02T12:34:56",
                    lat = i.toDouble(),
                    lon = i.toDouble(),
                )
            )
        }
        return StoryResponse(error = false, message = "Success", listStory = listStory)

    }

    override fun getStoriesByLocation(token: String, location: Int): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Call<StoryUploadResponse> {
        TODO("Not yet implemented")
    }
}
