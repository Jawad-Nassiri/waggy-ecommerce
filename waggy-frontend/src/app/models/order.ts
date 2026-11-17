import { OrderItem } from './orderItem';

export interface Order {
  id: number;
  userName: string;
  userId: number;
  orderDate: string;
  items: OrderItem[];
  totalPrice: number;
  status: string;
}
