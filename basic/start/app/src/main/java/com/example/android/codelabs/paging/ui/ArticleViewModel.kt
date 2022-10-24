/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.codelabs.paging.data.Article
import com.example.android.codelabs.paging.data.ArticleRepository
import kotlinx.coroutines.flow.Flow

private const val ITEMS_PER_PAGE = 50

/**
 * ViewModel for the [ArticleActivity] screen.
 * The ViewModel works with the [ArticleRepository] to get the data.
 */
class ArticleViewModel(
    repository: ArticleRepository,
) : ViewModel() {

    /**
     * Stream of [Article]s for the UI.
     */
    val items: Flow<PagingData<Article>> = Pager(
        //로드 대기 시간, 초기 로드의 크기 요청 등 PagingSource에서 콘텐츠를 로드하는 방법에 관한 옵션을 설정
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
                            //pageSize - 각 페이지에 로드해야 하는 항목 수 (필수 param)
                            //사용자가 스크롤할 때 메모리를 낭비하지 않으려면 PagingConfig에서 maxSize 매개변수를 설정 (기본적으로 Paging은 로드하는 모든 페이지를 메모리에 유지)
                            //enablePlaceholders - true인 경우 아직 로드되지 않은 콘텐츠의 자리표시자로 null 항목을 반환. 이렇게 하면 어댑터에 자리표시자 뷰를 표시할 수 있습니다.

        pagingSourceFactory = { repository.articlePagingSource() }
    ).flow
        .cachedIn(viewModelScope)   //구성 또는 탐색 변경사항에도 페이징 상태를 유지(HOW? androidx.lifecycle.viewModelScope를 전달)
}
