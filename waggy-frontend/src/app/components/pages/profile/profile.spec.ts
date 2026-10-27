import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Profile } from './profile';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { CartService } from '../../../services/cart.service';
import { OrderService } from '../../../services/order.service';
import { ImageUrlService } from '../../../services/image-url.service';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { Order } from '../../../models/order';

describe('Profile', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;

  let authService: {
    currentUser: ReturnType<typeof vi.fn>;
    logout: ReturnType<typeof vi.fn>;
  };

  let router: { navigate: ReturnType<typeof vi.fn> };

  let cartService: { clearCartCount: ReturnType<typeof vi.fn> };

  let orderService: { getOrders: ReturnType<typeof vi.fn> };

  let imageUrlService: { getImageUrl: ReturnType<typeof vi.fn> };

  const mockOrders = [
    {
      id: 1,
      orderDate: '2026-10-20',
      items: [],
      totalPrice: 25,
      status: 'PAID',
    },
    {
      id: 2,
      orderDate: '2026-10-21',
      items: [],
      totalPrice: 15,
      status: 'PENDING',
    },
  ] as Order[];

  beforeEach(async () => {
    authService = {
      currentUser: vi.fn(() => ({ id: 1, name: 'Test User' })),
      logout: vi.fn(() => of({})),
    };

    router = {
      navigate: vi.fn(() => Promise.resolve(true)),
    };

    cartService = {
      clearCartCount: vi.fn(),
    };

    orderService = {
      getOrders: vi.fn(() => of(mockOrders)),
    };

    imageUrlService = {
      getImageUrl: vi.fn((image: string) => `http://localhost:8080/${image}`),
    };

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: CartService, useValue: cartService },
        { provide: OrderService, useValue: orderService },
        { provide: ImageUrlService, useValue: imageUrlService },
      ],
    })
      .overrideComponent(Profile, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the current user and orders on initialization', () => {
    component.ngOnInit();

    expect(authService.currentUser).toHaveBeenCalled();
    expect(component.user).toEqual({ id: 1, name: 'Test User' });
    expect(orderService.getOrders).toHaveBeenCalled();
    expect(component.orders()).toEqual(mockOrders);
  });

  it('should load orders', () => {
    component.getOrders();

    expect(orderService.getOrders).toHaveBeenCalled();
    expect(component.orders()).toEqual(mockOrders);
  });

  it('should navigate to the home page and clear the cart count on logout', () => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/'], {
      state: { toastMessage: 'Logout successful!' },
    });
    expect(cartService.clearCartCount).toHaveBeenCalled();
  });

  it('should return the image URL from the image service', () => {
    const result = component.getImageUrl('dog.jpg');

    expect(result).toBe('http://localhost:8080/dog.jpg');
    expect(imageUrlService.getImageUrl).toHaveBeenCalledWith('dog.jpg');
  });

  it('should keep orders empty when no orders are returned', () => {
    orderService.getOrders.mockReturnValue(of([]));

    component.getOrders();

    expect(component.orders()).toEqual([]);
  });
});
