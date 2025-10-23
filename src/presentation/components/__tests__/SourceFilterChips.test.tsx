import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { SourceFilterChips } from '../SourceFilterChips';
import { SourceConfig } from '../../../domain/entities/SourceConfig';
import { SourceId } from '../../../domain/value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../../../domain/value-objects/SourceType';
import { URL } from '../../../domain/value-objects/URL';

describe('SourceFilterChips', () => {
  const createMockSource = (id: string, name: string): SourceConfig => {
    return new SourceConfig({
      id: new SourceId(id),
      name,
      type: new SourceType(SourceTypeEnum.SEARCH),
      apiUrl: new URL('https://api.example.com'),
      enabled: true,
    });
  };

  it('should render all sources as chips', () => {
    const sources = [
      createMockSource('source-1', 'Source 1'),
      createMockSource('source-2', 'Source 2'),
    ];

    render(
      <SourceFilterChips
        sources={sources}
        selectedSourceId={null}
        onSourceSelect={jest.fn()}
      />
    );

    expect(screen.getByText('All Sources')).toBeInTheDocument();
    expect(screen.getByText('Source 1')).toBeInTheDocument();
    expect(screen.getByText('Source 2')).toBeInTheDocument();
  });

  it('should call onSourceSelect when chip is clicked', () => {
    const onSourceSelect = jest.fn();
    const sources = [createMockSource('source-1', 'Source 1')];

    render(
      <SourceFilterChips
        sources={sources}
        selectedSourceId={null}
        onSourceSelect={onSourceSelect}
      />
    );

    fireEvent.click(screen.getByText('Source 1'));
    expect(onSourceSelect).toHaveBeenCalledWith('source-1');
  });

  it('should call onSourceSelect with null when "All Sources" is clicked', () => {
    const onSourceSelect = jest.fn();
    const sources = [createMockSource('source-1', 'Source 1')];

    render(
      <SourceFilterChips
        sources={sources}
        selectedSourceId="source-1"
        onSourceSelect={onSourceSelect}
      />
    );

    fireEvent.click(screen.getByText('All Sources'));
    expect(onSourceSelect).toHaveBeenCalledWith(null);
  });

  it('should show loading state', () => {
    render(
      <SourceFilterChips
        sources={[]}
        selectedSourceId={null}
        onSourceSelect={jest.fn()}
        isLoading={true}
      />
    );

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('should show error message', () => {
    render(
      <SourceFilterChips
        sources={[]}
        selectedSourceId={null}
        onSourceSelect={jest.fn()}
        error="Failed to load sources"
      />
    );

    expect(screen.getByText('Failed to load sources')).toBeInTheDocument();
  });

  it('should not render anything when no sources and not loading', () => {
    const { container } = render(
      <SourceFilterChips
        sources={[]}
        selectedSourceId={null}
        onSourceSelect={jest.fn()}
      />
    );

    expect(container.firstChild).toBeNull();
  });
});
