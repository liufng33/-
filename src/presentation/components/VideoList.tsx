import React from 'react';
import {
  Box,
  CircularProgress,
  Button,
  Alert,
  Typography,
} from '@mui/material';
import { VideoItem } from '../../domain/entities/VideoItem';
import { VideoCard } from './VideoCard';

interface VideoListProps {
  videos: VideoItem[];
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  onLoadMore: () => void;
  onVideoSelect: (video: VideoItem) => void;
  emptyMessage?: string;
}

export const VideoList: React.FC<VideoListProps> = ({
  videos,
  isLoading,
  error,
  hasMore,
  onLoadMore,
  onVideoSelect,
  emptyMessage = 'No videos found. Try a different search query.',
}) => {
  if (error && videos.length === 0) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        {error}
      </Alert>
    );
  }

  if (!isLoading && videos.length === 0) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '300px',
          textAlign: 'center',
        }}
      >
        <Typography variant="h6" color="text.secondary">
          {emptyMessage}
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: {
            xs: '1fr',
            sm: 'repeat(2, 1fr)',
            md: 'repeat(3, 1fr)',
            lg: 'repeat(4, 1fr)',
          },
          gap: 3,
        }}
      >
        {videos.map((video) => (
          <VideoCard
            key={`${video.getSourceId()}-${video.getId()}`}
            video={video}
            onSelect={onVideoSelect}
          />
        ))}
      </Box>

      {isLoading && videos.length === 0 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {hasMore && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Button
            variant="contained"
            onClick={onLoadMore}
            disabled={isLoading}
            size="large"
          >
            {isLoading ? (
              <>
                <CircularProgress size={20} sx={{ mr: 1 }} />
                Loading...
              </>
            ) : (
              'Load More'
            )}
          </Button>
        </Box>
      )}

      {error && videos.length > 0 && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      )}
    </Box>
  );
};
