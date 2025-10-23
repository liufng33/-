import { URL } from '../value-objects/URL';
import { ParseRule } from './ParseRule';

export interface ParserConfigProps {
  id: string;
  name: string;
  urlPattern: string;
  baseUrl?: URL;
  rules: ParseRule[];
  headers?: Record<string, string>;
  timeout?: number;
  enabled?: boolean;
}

export class ParserConfig {
  private readonly id: string;
  private name: string;
  private readonly urlPattern: string;
  private readonly baseUrl?: URL;
  private rules: ParseRule[];
  private headers: Record<string, string>;
  private timeout: number;
  private enabled: boolean;

  constructor(props: ParserConfigProps) {
    if (!props.id || props.id.trim().length === 0) {
      throw new Error('ParserConfig id cannot be empty');
    }
    if (!props.name || props.name.trim().length === 0) {
      throw new Error('ParserConfig name cannot be empty');
    }
    if (!props.urlPattern || props.urlPattern.trim().length === 0) {
      throw new Error('ParserConfig urlPattern cannot be empty');
    }
    if (props.timeout !== undefined && props.timeout <= 0) {
      throw new Error('ParserConfig timeout must be positive');
    }

    this.id = props.id.trim();
    this.name = props.name.trim();
    this.urlPattern = props.urlPattern.trim();
    this.baseUrl = props.baseUrl;
    this.rules = props.rules || [];
    this.headers = props.headers || {};
    this.timeout = props.timeout || 30000;
    this.enabled = props.enabled ?? true;
  }

  getId(): string {
    return this.id;
  }

  getName(): string {
    return this.name;
  }

  getUrlPattern(): string {
    return this.urlPattern;
  }

  getBaseUrl(): URL | undefined {
    return this.baseUrl;
  }

  getRules(): ParseRule[] {
    return [...this.rules];
  }

  getActiveRules(): ParseRule[] {
    return this.rules.filter(rule => rule.isEnabled()).sort((a, b) => b.getPriority() - a.getPriority());
  }

  getHeaders(): Record<string, string> {
    return { ...this.headers };
  }

  getTimeout(): number {
    return this.timeout;
  }

  isEnabled(): boolean {
    return this.enabled;
  }

  setName(name: string): void {
    if (!name || name.trim().length === 0) {
      throw new Error('ParserConfig name cannot be empty');
    }
    this.name = name.trim();
  }

  addRule(rule: ParseRule): void {
    if (!this.rules.find(r => r.equals(rule))) {
      this.rules.push(rule);
    }
  }

  removeRule(ruleId: string): void {
    this.rules = this.rules.filter(rule => rule.getId() !== ruleId);
  }

  enable(): void {
    this.enabled = true;
  }

  disable(): void {
    this.enabled = false;
  }

  matchesUrl(url: string): boolean {
    try {
      const regex = new RegExp(this.urlPattern);
      return regex.test(url);
    } catch {
      return false;
    }
  }

  equals(other: ParserConfig): boolean {
    return this.id === other.id;
  }
}
