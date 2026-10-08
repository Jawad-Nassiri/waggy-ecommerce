import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { Gallery } from '../../gallery/gallery';
import { galleryImages } from '../../../data/gallery-images';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { CartService } from '../../../services/cart.service';
import { Order } from '../../../models/order';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-profile',
  imports: [Banner, Gallery, DatePipe],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  constructor(
    private authService: AuthService,
    private router: Router,
    private cartService: CartService,
  ) {}

  user: any = null;
  galleryImages = galleryImages;

  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  orders = signal<Order[]>([]);

  ngOnInit() {
    this.user = this.authService.currentUser();
    this.getOrders();
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/'], {
          state: { toastMessage: 'Logout successful!' },
        });
        this.cartService.clearCartCount();
      },
    });
  }

  getOrders() {
    this.cartService.getOrders().subscribe((orders) => {
      this.orders.set(orders);
      console.log(this.orders())

    });
  }
}
