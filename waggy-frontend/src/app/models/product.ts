export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  image: string;
  createdAt: string;
  category: {
  id: number;
}
}