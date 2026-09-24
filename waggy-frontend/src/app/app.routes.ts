import { Routes } from '@angular/router';
import { Home } from './components/pages/home/home';
import { Signup } from './components/pages/signup/signup';
import { Login } from './components/pages/login/login';
import { Profile } from './components/pages/profile/profile';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { Products } from './components/pages/products/products';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'signup', component: Signup, canActivate: [guestGuard] },
  { path: 'login', component: Login, canActivate: [guestGuard] },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'products', component: Products},
];