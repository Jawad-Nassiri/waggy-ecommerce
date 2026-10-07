import { OrderItem } from './orderItem';

export interface Order {
  id: number;
  orderDate: string;
  items: OrderItem[];
  totalPrice: number;
  status: string;
}
