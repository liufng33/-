export enum RuleType {
  REGEX = 'REGEX',
  CSS_SELECTOR = 'CSS_SELECTOR',
  XPATH = 'XPATH',
  JSON_PATH = 'JSON_PATH'
}

export interface ParseRuleProps {
  id: string;
  name: string;
  type: RuleType;
  pattern: string;
  extractField?: string;
  priority?: number;
  enabled?: boolean;
}

export class ParseRule {
  private readonly id: string;
  private readonly name: string;
  private readonly type: RuleType;
  private readonly pattern: string;
  private readonly extractField?: string;
  private readonly priority: number;
  private enabled: boolean;

  constructor(props: ParseRuleProps) {
    if (!props.id || props.id.trim().length === 0) {
      throw new Error('ParseRule id cannot be empty');
    }
    if (!props.name || props.name.trim().length === 0) {
      throw new Error('ParseRule name cannot be empty');
    }
    if (!props.pattern || props.pattern.trim().length === 0) {
      throw new Error('ParseRule pattern cannot be empty');
    }

    this.id = props.id.trim();
    this.name = props.name.trim();
    this.type = props.type;
    this.pattern = props.pattern.trim();
    this.extractField = props.extractField?.trim();
    this.priority = props.priority ?? 0;
    this.enabled = props.enabled ?? true;
  }

  getId(): string {
    return this.id;
  }

  getName(): string {
    return this.name;
  }

  getType(): RuleType {
    return this.type;
  }

  getPattern(): string {
    return this.pattern;
  }

  getExtractField(): string | undefined {
    return this.extractField;
  }

  getPriority(): number {
    return this.priority;
  }

  isEnabled(): boolean {
    return this.enabled;
  }

  enable(): void {
    this.enabled = true;
  }

  disable(): void {
    this.enabled = false;
  }

  equals(other: ParseRule): boolean {
    return this.id === other.id;
  }
}
