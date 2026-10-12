import { Component, signal } from '@angular/core';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../services/order.service';


@Component({
  selector: 'app-payment-success',
  imports: [RouterLink],
  templateUrl: './payment-success.html',
  styleUrl: './payment-success.css',
})
export class PaymentSuccess {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) { }
  
  isValid = signal(false);

  ngOnInit() {
    const orderId = Number(this.route.snapshot.queryParamMap.get('orderId'));

    this.orderService.getOrders().subscribe((orders) => {
      const order = orders.find((order) => order.id === orderId);

      if (order?.status === 'PAID') {
        this.isValid.set(true)
      } else {
        this.router.navigate(['/cart']);
      }
    });
  }
}
