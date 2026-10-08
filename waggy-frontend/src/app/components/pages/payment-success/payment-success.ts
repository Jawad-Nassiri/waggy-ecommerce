import { Component, signal } from '@angular/core';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-payment-success',
  imports: [RouterLink],
  templateUrl: './payment-success.html',
  styleUrl: './payment-success.css',
})
export class PaymentSuccess {
  constructor(
    private cartService: CartService,
    private route: ActivatedRoute,
    private router: Router
  ) { }
  
  isValid = signal(false);

  ngOnInit() {
    const orderId = Number(this.route.snapshot.queryParamMap.get('orderId'));

    this.cartService.getOrders().subscribe((orders) => {
      const order = orders.find((order) => order.id === orderId);

      if (order?.status === 'PAID') {
        this.cartService.clearCart().subscribe();
        this.isValid.set(true)
      } else {
        this.router.navigate(['/cart']);
      }
    });
  }
}
