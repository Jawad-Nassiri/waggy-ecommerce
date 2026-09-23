import { HttpInterceptorFn } from '@angular/common/http';

// for every http request, include the authentication cookie.

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const request = req.clone({
    withCredentials: true,
  });

  return next(request);
};
