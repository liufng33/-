import React from 'react';
import { Box, Chip, CircularProgress, Alert } from '@mui/material';
import { SourceConfig } from '../../domain/entities/SourceConfig';

interface SourceFilterChipsProps {
  sources: SourceConfig[];
  selectedSourceId: string | null;
  onSourceSelect: (sourceId: string | null) => void;
  isLoading?: boolean;
  error?: string | null;
}

export const SourceFilterChips: React.FC<SourceFilterChipsProps> = ({
  sources,
  selectedSourceId,
  onSourceSelect,
  isLoading = false,
  error = null,
}) => {
  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {error}
      </Alert>
    );
  }

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
        <CircularProgress size={24} />
      </Box>
    );
  }

  if (sources.length === 0) {
    return null;
  }

  return (
    <Box
      sx={{
        display: 'flex',
        gap: 1,
        flexWrap: 'wrap',
        mb: 3,
        alignItems: 'center',
      }}
    >
      <Chip
        label="All Sources"
        onClick={() => onSourceSelect(null)}
        color={selectedSourceId === null ? 'primary' : 'default'}
        variant={selectedSourceId === null ? 'filled' : 'outlined'}
        sx={{ fontWeight: selectedSourceId === null ? 600 : 400 }}
      />
      {sources.map((source) => (
        <Chip
          key={source.getId().getValue()}
          label={source.getName()}
          onClick={() => onSourceSelect(source.getId().getValue())}
          color={selectedSourceId === source.getId().getValue() ? 'primary' : 'default'}
          variant={selectedSourceId === source.getId().getValue() ? 'filled' : 'outlined'}
          sx={{
            fontWeight: selectedSourceId === source.getId().getValue() ? 600 : 400,
          }}
        />
      ))}
    </Box>
  );
};
