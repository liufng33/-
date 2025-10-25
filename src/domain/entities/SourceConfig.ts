import { SourceId } from '../value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../value-objects/SourceType';
import { URL } from '../value-objects/URL';

export interface SourceConfigProps {
  id: SourceId;
  name: string;
  type: SourceType;
  apiUrl: URL;
  apiKey?: string;
  enabled?: boolean;
  priority?: number;
  rateLimit?: number;
  metadata?: Record<string, unknown>;
}

export class SourceConfig {
  private readonly id: SourceId;
  private name: string;
  private readonly type: SourceType;
  private apiUrl: URL;
  private apiKey?: string;
  private enabled: boolean;
  private priority: number;
  private rateLimit?: number;
  private metadata?: Record<string, unknown>;

  constructor(props: SourceConfigProps) {
    if (!props.name || props.name.trim().length === 0) {
      throw new Error('SourceConfig name cannot be empty');
    }
    if (props.priority !== undefined && props.priority < 0) {
      throw new Error('SourceConfig priority cannot be negative');
    }
    if (props.rateLimit !== undefined && props.rateLimit <= 0) {
      throw new Error('SourceConfig rateLimit must be positive');
    }

    this.id = props.id;
    this.name = props.name.trim();
    this.type = props.type;
    this.apiUrl = props.apiUrl;
    this.apiKey = props.apiKey;
    this.enabled = props.enabled ?? true;
    this.priority = props.priority ?? 0;
    this.rateLimit = props.rateLimit;
    this.metadata = props.metadata;
  }

  getId(): SourceId {
    return this.id;
  }

  getName(): string {
    return this.name;
  }

  getType(): SourceType {
    return this.type;
  }

  getApiUrl(): URL {
    return this.apiUrl;
  }

  getApiKey(): string | undefined {
    return this.apiKey;
  }

  isEnabled(): boolean {
    return this.enabled;
  }

  getPriority(): number {
    return this.priority;
  }

  getRateLimit(): number | undefined {
    return this.rateLimit;
  }

  getMetadata(): Record<string, unknown> | undefined {
    return this.metadata;
  }

  setName(name: string): void {
    if (!name || name.trim().length === 0) {
      throw new Error('SourceConfig name cannot be empty');
    }
    this.name = name.trim();
  }

  setApiUrl(apiUrl: URL): void {
    this.apiUrl = apiUrl;
  }

  setApiKey(apiKey: string | undefined): void {
    this.apiKey = apiKey;
  }

  enable(): void {
    this.enabled = true;
  }

  disable(): void {
    this.enabled = false;
  }

  setPriority(priority: number): void {
    if (priority < 0) {
      throw new Error('SourceConfig priority cannot be negative');
    }
    this.priority = priority;
  }

  equals(other: SourceConfig): boolean {
    return this.id.equals(other.id);
  }

  isSearchSource(): boolean {
    return this.type.getValue() === SourceTypeEnum.SEARCH;
  }

  isParserSource(): boolean {
    return this.type.getValue() === SourceTypeEnum.PARSER;
  }
}
