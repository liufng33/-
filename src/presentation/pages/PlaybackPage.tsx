import React, { useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  Button,
  Chip,
  Stack,
  Divider,
  IconButton,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { usePlaybackViewModel } from '../hooks/usePlaybackViewModel';
import { PlaybackViewModel } from '../viewmodels/PlaybackViewModel';
import { VideoItem } from '../../domain/entities/VideoItem';

interface PlaybackPageProps {
  viewModel: PlaybackViewModel;
  video: VideoItem;
  onBack: () => void;
}

export const PlaybackPage: React.FC<PlaybackPageProps> = ({ viewModel, video, onBack }) => {
  const { state, loadPlaybackLinks, selectPlaybackLink } = usePlaybackViewModel(viewModel);

  useEffect(() => {
    loadPlaybackLinks(video);
  }, [video, loadPlaybackLinks]);

  const formatDuration = (seconds?: number): string => {
    if (!seconds) return 'Unknown';
    
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
      return `${hours}h ${minutes}m ${secs}s`;
    }
    return `${minutes}m ${secs}s`;
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 3 }}>
        <IconButton onClick={onBack} sx={{ mb: 2 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 700 }}>
          {video.getTitle()}
        </Typography>
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          {state.isLoading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : state.error ? (
            <Alert severity="error">{state.error}</Alert>
          ) : (
            <>
              {state.selectedLink && (
                <Box sx={{ mb: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Now Playing
                  </Typography>
                  <Box
                    sx={{
                      width: '100%',
                      aspectRatio: '16/9',
                      backgroundColor: 'black',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      borderRadius: 1,
                      mb: 2,
                    }}
                  >
                    <video
                      controls
                      style={{ width: '100%', height: '100%' }}
                      src={state.selectedLink.getUrl().getValue()}
                    >
                      Your browser does not support the video tag.
                    </video>
                  </Box>
                  <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                    <Chip
                      label={state.selectedLink.getQuality().toString()}
                      color="primary"
                      size="small"
                    />
                    <Chip
                      label={state.selectedLink.getFormat()}
                      variant="outlined"
                      size="small"
                    />
                  </Box>
                </Box>
              )}

              {state.playbackLinks.length > 1 && (
                <>
                  <Divider sx={{ my: 3 }} />
                  <Typography variant="h6" gutterBottom>
                    Available Quality Options
                  </Typography>
                  <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                    {state.playbackLinks.map((link, index) => (
                      <Button
                        key={index}
                        variant={
                          state.selectedLink?.equals(link) ? 'contained' : 'outlined'
                        }
                        onClick={() => selectPlaybackLink(link)}
                        size="small"
                      >
                        {link.getQuality().toString()} - {link.getFormat()}
                      </Button>
                    ))}
                  </Stack>
                </>
              )}
            </>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Video Details
          </Typography>
          
          {video.getDescription() && (
            <Typography variant="body1" paragraph>
              {video.getDescription()}
            </Typography>
          )}

          <Stack spacing={1}>
            {video.getDuration() && (
              <Box>
                <Typography variant="body2" color="text.secondary" component="span">
                  Duration:{' '}
                </Typography>
                <Typography variant="body2" component="span">
                  {formatDuration(video.getDuration())}
                </Typography>
              </Box>
            )}
            
            {video.getPublishDate() && (
              <Box>
                <Typography variant="body2" color="text.secondary" component="span">
                  Published:{' '}
                </Typography>
                <Typography variant="body2" component="span">
                  {new Date(video.getPublishDate()!).toLocaleDateString()}
                </Typography>
              </Box>
            )}

            <Box>
              <Typography variant="body2" color="text.secondary" component="span">
                Source:{' '}
              </Typography>
              <Typography variant="body2" component="span">
                {video.getSourceId()}
              </Typography>
            </Box>

            <Box>
              <Typography variant="body2" color="text.secondary" component="span">
                URL:{' '}
              </Typography>
              <Typography
                variant="body2"
                component="a"
                href={video.getUrl().getValue()}
                target="_blank"
                rel="noopener noreferrer"
                sx={{ wordBreak: 'break-all' }}
              >
                {video.getUrl().getValue()}
              </Typography>
            </Box>
          </Stack>
        </CardContent>
      </Card>
    </Container>
  );
};
