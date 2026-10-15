import { Component, signal } from '@angular/core';
import { UserService } from '../../../../services/user.service';
import { ProductService } from '../../../../services/product.service';
import { Product } from '../../../../models/product';
import { User } from '../../../../models/user';
import { OrderService } from '../../../../services/order.service';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';



@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  constructor(
    private userService: UserService,
    private productService: ProductService,
    private orderService: OrderService
  ) {}

  usersCount = signal(0);
  lastUsers = signal<User[]>([]);
  adminsCount = signal(0);
  productsCount = signal(0);
  lastProducts = signal<Product[]>([]);
  ordersCount = signal(0);

  getUsers() {
    this.userService.getAllUsers().subscribe((res) => {
      let admins = res.filter((admin) => admin.role === 'ADMIN');

      this.usersCount.set(res.length);
      this.adminsCount.set(admins.length);
      this.lastUsers.set(res.slice(-5));
    });
  }

  getProducts() {
    this.productService.getProducts().subscribe((res) => {
      this.productsCount.set(res.length);
      this.lastProducts.set(res.slice(-5));
    });
  }

  getAllOrders() {
    this.orderService.getOrdersCount().subscribe(res => {
      this.ordersCount.set(res);
    });
  }

  ngOnInit() {
    this.getUsers();
    this.getProducts();
    this.getAllOrders();
  }
}
