import { useState, useEffect, useCallback } from 'react';
import { PlaybackViewModel, PlaybackState } from '../viewmodels/PlaybackViewModel';
import { VideoItem } from '../../domain/entities/VideoItem';
import { PlaybackLink } from '../../domain/entities/PlaybackLink';

export function usePlaybackViewModel(viewModel: PlaybackViewModel) {
  const [state, setState] = useState<PlaybackState>(viewModel.getState());

  useEffect(() => {
    const unsubscribe = viewModel.subscribeToState(setState);
    return unsubscribe;
  }, [viewModel]);

  const loadPlaybackLinks = useCallback(
    (video: VideoItem, refreshExpired: boolean = true) => {
      return viewModel.loadPlaybackLinks(video, refreshExpired);
    },
    [viewModel]
  );

  const selectPlaybackLink = useCallback(
    (link: PlaybackLink) => {
      viewModel.selectPlaybackLink(link);
    },
    [viewModel]
  );

  const clearState = useCallback(() => {
    viewModel.clearState();
  }, [viewModel]);

  return {
    state,
    loadPlaybackLinks,
    selectPlaybackLink,
    clearState,
  };
}
