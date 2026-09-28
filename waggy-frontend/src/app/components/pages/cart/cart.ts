import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { CartService } from '../../../services/cart.service';
import { CartItem } from '../../../models/cart-item';
import { Cart } from '../../../models/cart';
import { RouterLink } from '@angular/router';


@Component({
  selector: 'app-cart',
  imports: [Banner, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class CartComponent {
  cartItems = signal<CartItem[]>([]);

  constructor(private cartService: CartService) {}

  getMyCart(): void {
    this.cartService.getMyCart().subscribe((response: Cart) => {
      this.cartItems.set(response.items);
    });
  }

  ngOnInit(): void {
    this.getMyCart();
  }
}
