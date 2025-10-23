import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { VideoCard } from '../VideoCard';
import { VideoItem } from '../../../domain/entities/VideoItem';
import { URL } from '../../../domain/value-objects/URL';

describe('VideoCard', () => {
  const createMockVideo = (duration?: number): VideoItem => {
    return new VideoItem({
      id: 'video-1',
      title: 'Test Video',
      url: new URL('https://example.com/video'),
      sourceId: 'test-source',
      thumbnailUrl: new URL('https://example.com/thumb.jpg'),
      duration: duration !== undefined ? duration : 180,
      description: 'Test description',
      publishDate: new Date('2024-01-01'),
    });
  };

  it('should render video title', () => {
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('Test Video')).toBeInTheDocument();
  });

  it('should render video description', () => {
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('Test description')).toBeInTheDocument();
  });

  it('should render thumbnail image', () => {
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    const image = screen.getByAltText('Test Video') as HTMLImageElement;
    expect(image).toBeInTheDocument();
    expect(image.src).toContain('thumb.jpg');
  });

  it('should format and display duration', () => {
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('3:00')).toBeInTheDocument();
  });

  it('should format duration with hours', () => {
    const video = createMockVideo(3661);
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('1:01:01')).toBeInTheDocument();
  });

  it('should display publish date', () => {
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('1/1/2024')).toBeInTheDocument();
  });

  it('should call onSelect when clicked', () => {
    const onSelect = jest.fn();
    const video = createMockVideo();
    render(<VideoCard video={video} onSelect={onSelect} />);
    
    const card = screen.getByText('Test Video').closest('button');
    fireEvent.click(card!);

    expect(onSelect).toHaveBeenCalledWith(video);
  });

  it('should handle missing optional fields', () => {
    const video = new VideoItem({
      id: 'video-1',
      title: 'Minimal Video',
      url: new URL('https://example.com/video'),
      sourceId: 'test-source',
    });

    render(<VideoCard video={video} onSelect={jest.fn()} />);
    
    expect(screen.getByText('Minimal Video')).toBeInTheDocument();
    expect(screen.queryByText(/:/)).not.toBeInTheDocument();
  });
});
