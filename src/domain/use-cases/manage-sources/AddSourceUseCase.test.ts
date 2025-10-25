import { AddSourceUseCase } from './AddSourceUseCase';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceTypeEnum } from '../../value-objects/SourceType';

describe('AddSourceUseCase', () => {
  let useCase: AddSourceUseCase;
  let mockRepository: jest.Mocked<ISourceConfigRepository>;

  beforeEach(() => {
    mockRepository = {
      findById: jest.fn(),
      findAll: jest.fn(),
      findByType: jest.fn(),
      findEnabled: jest.fn(),
      save: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      exists: jest.fn()
    };

    useCase = new AddSourceUseCase(mockRepository);
  });

  describe('execute', () => {
    it('should add a new source successfully', async () => {
      mockRepository.exists.mockResolvedValue(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        id: 'test-source',
        name: 'Test Source',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'https://api.example.com',
        enabled: true,
        priority: 10
      };

      const result = await useCase.execute(request);

      expect(result.getId().getValue()).toBe('test-source');
      expect(result.getName()).toBe('Test Source');
      expect(result.isEnabled()).toBe(true);
      expect(result.getPriority()).toBe(10);
      expect(mockRepository.save).toHaveBeenCalledWith(result);
    });

    it('should throw error if source with same id already exists', async () => {
      mockRepository.exists.mockResolvedValue(true);

      const request = {
        id: 'existing-source',
        name: 'Test Source',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'https://api.example.com'
      };

      await expect(useCase.execute(request)).rejects.toThrow(
        "Source with id 'existing-source' already exists"
      );
      expect(mockRepository.save).not.toHaveBeenCalled();
    });

    it('should throw error for invalid URL', async () => {
      mockRepository.exists.mockResolvedValue(false);

      const request = {
        id: 'test-source',
        name: 'Test Source',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'not-a-valid-url'
      };

      await expect(useCase.execute(request)).rejects.toThrow('Invalid URL');
    });

    it('should throw error for empty name', async () => {
      mockRepository.exists.mockResolvedValue(false);

      const request = {
        id: 'test-source',
        name: '',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'https://api.example.com'
      };

      await expect(useCase.execute(request)).rejects.toThrow('SourceConfig name cannot be empty');
    });

    it('should throw error for negative priority', async () => {
      mockRepository.exists.mockResolvedValue(false);

      const request = {
        id: 'test-source',
        name: 'Test Source',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'https://api.example.com',
        priority: -1
      };

      await expect(useCase.execute(request)).rejects.toThrow('SourceConfig priority cannot be negative');
    });

    it('should handle optional fields correctly', async () => {
      mockRepository.exists.mockResolvedValue(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        id: 'test-source',
        name: 'Test Source',
        type: SourceTypeEnum.SEARCH,
        apiUrl: 'https://api.example.com',
        apiKey: 'secret-key',
        rateLimit: 100,
        metadata: { custom: 'value' }
      };

      const result = await useCase.execute(request);

      expect(result.getApiKey()).toBe('secret-key');
      expect(result.getRateLimit()).toBe(100);
      expect(result.getMetadata()).toEqual({ custom: 'value' });
    });
  });
});
