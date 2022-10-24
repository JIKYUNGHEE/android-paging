package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.time.LocalDateTime
import kotlin.math.max

class ArticlePagingSource : PagingSource<Int, Article>() {

    private val firstArticleCreatedTime = LocalDateTime.now()
    private val STARTING_KEY = 0

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    /**
     * 사용자가 스크롤할 때 표시할 더 많은 데이터를 비동기식으로 가져오기 위해 Paging 라이브러리에서 load() 함수를 호출
     *
     * @param: LoadParams.key    //load 가 처음 호출되는 경우, null
     * @return: LoadResult   //type: Page, Error, Invalid
     *                      //arguments:
     *                          필수-data, prevKey, nextKey
     *                          선택-itemBefore, itemAfter
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        // Start paging with the STARTING_KEY if this is the first load
        val start = params.key ?: STARTING_KEY

        // Load as many items as hinted by params.loadSize
        val range = start.until(start + params.loadSize)

        return LoadResult.Page(
            data = range.map { number ->
                Article(
                    // Generate consecutive increasing numbers as the article id
                    id = number,
                    title = "Article $number",
                    description = "This describes article $number",
                    created = firstArticleCreatedTime.minusDays(number.toLong())
                )
            },

            // Make sure we don't try to load items behind the STARTING_KEY
            prevKey = when (start) {
                STARTING_KEY -> null
                else -> ensureValidKey(key = range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )

    }

    /**
     *  Paging 라이브러리가 UI 관련 항목을 새로고침해야 할 때 호출(지원 PagingSource의 데이터가 변경되었기 때문)
     *
     *  [invalidation]
     *  PagingSource의 기본 데이터가 변경되었으며 UI에서 업데이트해야 하는 상황
     *  -> Paging 라이브러리에서 무효화가 발생하는 이유
            - PagingAdapter에서 refresh()를 호출
            - PagingSource에서 invalidate()를 호출

     *
     *  @param: PagingState
     *  @return: Int    //LoadParams 인수를 통해 새 PagingSource의 다음 load() 메서드 호출에 전달 됨
     */
    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer
        val anchorPosition = state.anchorPosition ?: return null    // 읽을 때 데이터를 성공적으로 가져온 마지막 색인은 anchorPosition
        val article = state.closestItemToPosition(anchorPosition) ?: return null    //새로고침할 때는 anchorPosition에 가장 가까운 Article 키를 가져와 로드 키로 사용
        return ensureValidKey(key = article.id - (state.config.pageSize / 2))
    }
}