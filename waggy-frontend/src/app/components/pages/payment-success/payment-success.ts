import { Component, signal } from '@angular/core';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { Gallery } from '../../gallery/gallery';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';


@Component({
  selector: 'app-payment-success',
  imports: [RouterLink, Gallery, Banner],
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

  galleryImages = galleryImages;

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
