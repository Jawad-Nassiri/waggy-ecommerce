import { Routes } from '@angular/router';
import { Home } from './components/pages/home/home';
import { Signup } from './components/pages/signup/signup';
import { Login } from './components/pages/login/login';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'signup', component: Signup },
    { path: 'login', component: Login}
];
