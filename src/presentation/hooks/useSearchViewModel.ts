import { useState, useEffect, useCallback } from 'react';
import { SearchViewModel, SearchState, SourcesState } from '../viewmodels/SearchViewModel';

export function useSearchViewModel(viewModel: SearchViewModel) {
  const [searchState, setSearchState] = useState<SearchState>(viewModel.getSearchState());
  const [sourcesState, setSourcesState] = useState<SourcesState>(viewModel.getSourcesState());

  useEffect(() => {
    const unsubscribeSearch = viewModel.subscribeToSearchState(setSearchState);
    const unsubscribeSources = viewModel.subscribeToSourcesState(setSourcesState);

    return () => {
      unsubscribeSearch();
      unsubscribeSources();
    };
  }, [viewModel]);

  const search = useCallback(
    (query: string, sourceId?: string) => {
      return viewModel.search(query, sourceId);
    },
    [viewModel]
  );

  const loadMore = useCallback(() => {
    return viewModel.loadMore();
  }, [viewModel]);

  const clearSearch = useCallback(() => {
    viewModel.clearSearch();
  }, [viewModel]);

  const loadSources = useCallback(() => {
    return viewModel.loadSources();
  }, [viewModel]);

  return {
    searchState,
    sourcesState,
    search,
    loadMore,
    clearSearch,
    loadSources,
  };
}
