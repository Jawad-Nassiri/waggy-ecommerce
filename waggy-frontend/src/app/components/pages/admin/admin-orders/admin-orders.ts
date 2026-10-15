import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfirmModal } from '../confirm-modal/confirm-modal';
import { OrderService } from '../../../../services/order.service';
import { Order } from '../../../../models/order';
import { ImageUrlService } from '../../../../services/image-url.service';
import { Toast } from '../../../toast/toast';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-admin-orders',
  imports: [DatePipe, NgClass, FormsModule, Toast, ConfirmModal, DecimalPipe],
  templateUrl: './admin-orders.html',
  styleUrl: './admin-orders.css',
})
export class AdminOrders implements OnInit {
  constructor(
    private orderService: OrderService,
    private imageUrlService: ImageUrlService,
  ) {}

  orders = signal<any[]>([]);
  showConfirm = signal(false);
  confirmMessage = 'Are you sure you want to delete this order?';
  isDetailsOpen = signal(false);
  selectedOrder = signal<Order | null>(null);
  isStatusOpen = false;

  confirmAction: () => void = () => {};

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  onConfirmYes(): void {
    this.showConfirm.set(false);
    this.confirmAction();
  }

  openDetails(order: Order): void {
    this.selectedOrder.set(order);
    this.isDetailsOpen.set(true);
  }

  closeDetails(): void {
    this.isDetailsOpen.set(false);
    this.selectedOrder.set(null);
  }

  ngOnInit(): void {
    this.getAllOrders();
  }

  getAllOrders(): void {
    this.orderService.getAllOrders().subscribe({
      next: (res) => {
        this.orders.set(res);
      },
      error: (err) => console.error('Failed to load orders', err.error),
    });
  }

  getImageUrl(image: string): string {
    return this.imageUrlService.getImageUrl(image);
  }

  deleteOrder(id: number): void {
    this.confirmMessage = 'Are you sure you want to delete this order?';
    this.confirmAction = () => {
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          this.orders.update((orders) => orders.filter((order) => order.id !== id));
          this.showToast.set(true);
          this.toastTitle = 'Success';
          this.toastMessage = 'Order deleted successfully!';
          this.toastType = 'success';
        },
        error: () => {
          this.showToast.set(true);
          this.toastTitle = 'Error';
          this.toastMessage = 'Failed to delete order!';
          this.toastType = 'error';
        },
      });
    };

    this.showConfirm.set(true);
  }
}
