import React, { useState } from 'react';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import { HomePage } from './pages/HomePage';
import { PlaybackPage } from './pages/PlaybackPage';
import { SearchViewModel } from './viewmodels/SearchViewModel';
import { PlaybackViewModel } from './viewmodels/PlaybackViewModel';
import { VideoItem } from '../domain/entities/VideoItem';

interface AppProps {
  searchViewModel: SearchViewModel;
  playbackViewModel: PlaybackViewModel;
}

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },
  },
});

export const App: React.FC<AppProps> = ({ searchViewModel, playbackViewModel }) => {
  const [selectedVideo, setSelectedVideo] = useState<VideoItem | null>(null);

  const handleVideoSelect = (video: VideoItem) => {
    setSelectedVideo(video);
  };

  const handleBack = () => {
    setSelectedVideo(null);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {selectedVideo ? (
        <PlaybackPage
          viewModel={playbackViewModel}
          video={selectedVideo}
          onBack={handleBack}
        />
      ) : (
        <HomePage viewModel={searchViewModel} onVideoSelect={handleVideoSelect} />
      )}
    </ThemeProvider>
  );
};
