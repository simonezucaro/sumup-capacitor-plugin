import { registerPlugin } from '@capacitor/core';
import type { SumupCapacitorPlugin } from './definitions';

const SumupCapacitor = registerPlugin<SumupCapacitorPlugin>('SumupCapacitor');

export * from './definitions';
export { SumupCapacitor };
