import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, of } from 'rxjs';

export const resetPasswordGuard: CanActivateFn = (route) => {
  const http = inject(HttpClient);
  const router = inject(Router);

  const token = route.queryParams['token'];

  if (!token) {
    return router.createUrlTree(['/login']);
  }

  return http.get(`https://waggy-backend-vsrz.onrender.com/auth/validate-reset-token=${token}`).pipe(
    map(() => true),
    catchError(() => of(router.createUrlTree(['/login']))),
  );
};
