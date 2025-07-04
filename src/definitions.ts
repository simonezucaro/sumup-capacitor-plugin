export interface SumupCapacitorPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
