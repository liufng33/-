import React from 'react';
import {
  Card,
  CardActionArea,
  CardContent,
  CardMedia,
  Typography,
  Chip,
  Box,
} from '@mui/material';
import { VideoItem } from '../../domain/entities/VideoItem';

interface VideoCardProps {
  video: VideoItem;
  onSelect: (video: VideoItem) => void;
}

export const VideoCard: React.FC<VideoCardProps> = ({ video, onSelect }) => {
  const formatDuration = (seconds?: number): string => {
    if (!seconds) return '';
    
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
      return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDate = (date?: Date): string => {
    if (!date) return '';
    return new Date(date).toLocaleDateString();
  };

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 6,
        },
      }}
    >
      <CardActionArea onClick={() => onSelect(video)} sx={{ flexGrow: 1 }}>
        {video.getThumbnailUrl() && (
          <CardMedia
            component="img"
            height="180"
            image={video.getThumbnailUrl()?.getValue()}
            alt={video.getTitle()}
            sx={{ objectFit: 'cover' }}
          />
        )}
        <CardContent>
          <Typography
            gutterBottom
            variant="h6"
            component="div"
            sx={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              minHeight: '3em',
            }}
          >
            {video.getTitle()}
          </Typography>
          
          {video.getDescription() && (
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                display: '-webkit-box',
                WebkitLineClamp: 3,
                WebkitBoxOrient: 'vertical',
                mb: 2,
              }}
            >
              {video.getDescription()}
            </Typography>
          )}

          <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', alignItems: 'center' }}>
            {video.getDuration() && (
              <Chip
                label={formatDuration(video.getDuration())}
                size="small"
                variant="outlined"
              />
            )}
            {video.getPublishDate() && (
              <Chip
                label={formatDate(video.getPublishDate())}
                size="small"
                variant="outlined"
              />
            )}
          </Box>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};
