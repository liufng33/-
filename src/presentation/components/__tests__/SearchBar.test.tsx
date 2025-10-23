import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { SearchBar } from '../SearchBar';

describe('SearchBar', () => {
  it('should render with placeholder text', () => {
    render(<SearchBar onSearch={jest.fn()} placeholder="Search videos..." />);
    
    expect(screen.getByPlaceholderText('Search videos...')).toBeInTheDocument();
  });

  it('should call onSearch when form is submitted', async () => {
    const onSearch = jest.fn();
    render(<SearchBar onSearch={onSearch} />);

    const input = screen.getByPlaceholderText('Search videos...');
    await userEvent.type(input, 'test query');
    
    const form = input.closest('form');
    fireEvent.submit(form!);

    expect(onSearch).toHaveBeenCalledWith('test query');
  });

  it('should call onClear when clear button is clicked', async () => {
    const onClear = jest.fn();
    render(<SearchBar onSearch={jest.fn()} onClear={onClear} initialValue="test" />);

    const clearButton = screen.getByLabelText('clear');
    await userEvent.click(clearButton);

    expect(onClear).toHaveBeenCalled();
  });

  it('should show clear button only when there is text', async () => {
    render(<SearchBar onSearch={jest.fn()} />);

    expect(screen.queryByLabelText('clear')).not.toBeInTheDocument();

    const input = screen.getByPlaceholderText('Search videos...');
    await userEvent.type(input, 'test');

    expect(screen.getByLabelText('clear')).toBeInTheDocument();
  });

  it('should clear input when clear button is clicked', async () => {
    render(<SearchBar onSearch={jest.fn()} initialValue="initial value" />);

    const input = screen.getByPlaceholderText('Search videos...') as HTMLInputElement;
    expect(input.value).toBe('initial value');

    const clearButton = screen.getByLabelText('clear');
    await userEvent.click(clearButton);

    expect(input.value).toBe('');
  });
});
