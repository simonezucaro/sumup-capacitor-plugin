import { registerPlugin } from '@capacitor/core';
import type { SumupCapacitorPlugin } from './definitions';

const SumupCapacitorPlugin = registerPlugin<SumupCapacitorPlugin>('SumupCapacitor');

export * from './definitions';
export { SumupCapacitorPlugin };
