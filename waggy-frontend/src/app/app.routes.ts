import { Routes } from '@angular/router';
import { Home } from './components/pages/home/home';
import { Signup } from './components/pages/signup/signup';
import { Login } from './components/pages/login/login';
import { Profile } from './components/pages/profile/profile';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { Products } from './components/pages/products/products';
import { Blog } from './components/pages/blog/blog';
import { CartComponent } from './components/pages/cart/cart';
import { ProductDetail } from './components/pages/product-detail/product-detail';
import { Faq } from './components/pages/faq/faq';
import { PaymentSuccess } from './components/pages/payment-success/payment-success';
import { Admin } from './components/pages/admin/admin';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'signup', component: Signup, canActivate: [guestGuard] },
  { path: 'login', component: Login, canActivate: [guestGuard] },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'products', component: Products },
  { path: 'blog', component: Blog },
  { path: 'cart', component: CartComponent },
  { path: 'product-detail/:id', component: ProductDetail },
  { path: 'faq', component: Faq },
  { path: 'payments/success', component: PaymentSuccess, canActivate: [authGuard] },
  { path: 'payments/cancel', component: PaymentSuccess, canActivate: [authGuard] },
  {
    path: 'admin', component: Admin},
];
