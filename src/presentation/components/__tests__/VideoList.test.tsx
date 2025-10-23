import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { VideoList } from '../VideoList';
import { VideoItem } from '../../../domain/entities/VideoItem';
import { URL } from '../../../domain/value-objects/URL';

describe('VideoList', () => {
  const createMockVideo = (id: string, title: string): VideoItem => {
    return new VideoItem({
      id,
      title,
      url: new URL('https://example.com/video'),
      sourceId: 'test-source',
    });
  };

  it('should render video cards', () => {
    const videos = [
      createMockVideo('1', 'Video 1'),
      createMockVideo('2', 'Video 2'),
    ];

    render(
      <VideoList
        videos={videos}
        isLoading={false}
        error={null}
        hasMore={false}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.getByText('Video 1')).toBeInTheDocument();
    expect(screen.getByText('Video 2')).toBeInTheDocument();
  });

  it('should show loading spinner when loading and no videos', () => {
    render(
      <VideoList
        videos={[]}
        isLoading={true}
        error={null}
        hasMore={false}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('should show error message when error and no videos', () => {
    render(
      <VideoList
        videos={[]}
        isLoading={false}
        error="Search failed"
        hasMore={false}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.getByText('Search failed')).toBeInTheDocument();
  });

  it('should show empty message when no videos', () => {
    render(
      <VideoList
        videos={[]}
        isLoading={false}
        error={null}
        hasMore={false}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
        emptyMessage="No results found"
      />
    );

    expect(screen.getByText('No results found')).toBeInTheDocument();
  });

  it('should show Load More button when hasMore is true', () => {
    const videos = [createMockVideo('1', 'Video 1')];

    render(
      <VideoList
        videos={videos}
        isLoading={false}
        error={null}
        hasMore={true}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.getByText('Load More')).toBeInTheDocument();
  });

  it('should call onLoadMore when Load More button is clicked', () => {
    const onLoadMore = jest.fn();
    const videos = [createMockVideo('1', 'Video 1')];

    render(
      <VideoList
        videos={videos}
        isLoading={false}
        error={null}
        hasMore={true}
        onLoadMore={onLoadMore}
        onVideoSelect={jest.fn()}
      />
    );

    fireEvent.click(screen.getByText('Load More'));
    expect(onLoadMore).toHaveBeenCalled();
  });

  it('should disable Load More button when loading', () => {
    const videos = [createMockVideo('1', 'Video 1')];

    render(
      <VideoList
        videos={videos}
        isLoading={true}
        error={null}
        hasMore={true}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    const button = screen.getByRole('button', { name: /loading/i });
    expect(button).toBeDisabled();
  });

  it('should not show Load More button when hasMore is false', () => {
    const videos = [createMockVideo('1', 'Video 1')];

    render(
      <VideoList
        videos={videos}
        isLoading={false}
        error={null}
        hasMore={false}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.queryByText('Load More')).not.toBeInTheDocument();
  });

  it('should show error alert when error and videos exist', () => {
    const videos = [createMockVideo('1', 'Video 1')];

    render(
      <VideoList
        videos={videos}
        isLoading={false}
        error="Failed to load more"
        hasMore={true}
        onLoadMore={jest.fn()}
        onVideoSelect={jest.fn()}
      />
    );

    expect(screen.getByText('Video 1')).toBeInTheDocument();
    expect(screen.getByText('Failed to load more')).toBeInTheDocument();
  });
});
