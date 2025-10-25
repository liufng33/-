export enum QualityLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  ULTRA = 'ULTRA',
  UNKNOWN = 'UNKNOWN'
}

export class Quality {
  private readonly level: QualityLevel;
  private readonly resolution?: string;

  constructor(level: QualityLevel, resolution?: string) {
    this.level = level;
    this.resolution = resolution;
  }

  getLevel(): QualityLevel {
    return this.level;
  }

  getResolution(): string | undefined {
    return this.resolution;
  }

  equals(other: Quality): boolean {
    return this.level === other.level && this.resolution === other.resolution;
  }

  toString(): string {
    return this.resolution ? `${this.level} (${this.resolution})` : this.level;
  }
}
