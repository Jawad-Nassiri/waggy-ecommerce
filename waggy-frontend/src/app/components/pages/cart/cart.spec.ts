import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartComponent } from './cart';
import { CartService } from '../../../services/cart.service';
import { OrderService } from '../../../services/order.service';
import { ImageUrlService } from '../../../services/image-url.service';
import { CartItem } from '../../../models/cart-item';
import { Cart } from '../../../models/cart';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('CartComponent', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;

  let cartService: {
    getMyCart: ReturnType<typeof vi.fn>;
    updateCartItem: ReturnType<typeof vi.fn>;
    removeItemFromCart: ReturnType<typeof vi.fn>;
    clearCart: ReturnType<typeof vi.fn>;
    createPayment: ReturnType<typeof vi.fn>;
  };

  let orderService: {
    createOrder: ReturnType<typeof vi.fn>;
  };

  let imageUrlService: {
    getImageUrl: ReturnType<typeof vi.fn>;
  };

  const emptyCart = (): Cart => ({ id: 1, userId: 1, items: [] }) as Cart;

  const makeItem = (quantity = 1, stock = 5): CartItem =>
    ({
      productId: 1,
      productName: 'Dog Hoodie',
      price: 15,
      quantity,
      stock,
      subtotal: 15 * quantity,
      image: 'hoodie.jpg',
    }) as CartItem;

  beforeEach(async () => {
    cartService = {
      getMyCart: vi.fn(() => of(emptyCart())),
      updateCartItem: vi.fn(() => of({})),
      removeItemFromCart: vi.fn(() => of(emptyCart())),
      clearCart: vi.fn(() => of({})),
      createPayment: vi.fn(() => of({ checkoutUrl: 'https://example.com/checkout' })),
    };

    orderService = {
      createOrder: vi.fn(() => of({ id: 10 })),
    };

    imageUrlService = {
      getImageUrl: vi.fn((image: string) => `http://localhost:8080/${image}`),
    };

    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [
        { provide: CartService, useValue: cartService },
        { provide: OrderService, useValue: orderService },
        { provide: ImageUrlService, useValue: imageUrlService },
      ],
    })
      .overrideComponent(CartComponent, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('getMyCart', () => {
    it('should load cart items', () => {
      const items = [makeItem()];
      cartService.getMyCart.mockReturnValue(of({ id: 1, userId: 1, items } as Cart));

      component.getMyCart();

      expect(component.cartItems()).toEqual(items);
    });
  });

  describe('decreaseQty', () => {
    it('should decrease quantity and show success toast', () => {
      const item = makeItem(2);

      component.decreaseQty(item);

      expect(item.quantity).toBe(1);
      expect(cartService.updateCartItem).toHaveBeenCalledWith(1, 1);
      expect(component.showToast()).toBe(true);
      expect(component.toastTitle).toBe('Success');
    });

    it('should not decrease quantity below one', () => {
      const item = makeItem(1);

      component.decreaseQty(item);

      expect(item.quantity).toBe(1);
      expect(cartService.updateCartItem).not.toHaveBeenCalled();
    });

    it('should show an error when updating quantity fails', () => {
      const item = makeItem(2);
      cartService.updateCartItem.mockReturnValue(throwError(() => new Error('Update failed')));

      component.decreaseQty(item);

      expect(component.showToast()).toBe(true);
      expect(component.toastTitle).toBe('Error');
      expect(component.toastMessage).toBe('Failed to update product.');
    });
  });

  describe('increaseQty', () => {
    it('should increase quantity and show success toast', () => {
      const item = makeItem(1, 5);

      component.increaseQty(item);

      expect(item.quantity).toBe(2);
      expect(cartService.updateCartItem).toHaveBeenCalledWith(1, 2);
      expect(component.showToast()).toBe(true);
      expect(component.toastTitle).toBe('Success');
    });

    it('should block increasing quantity beyond stock', () => {
      const item = makeItem(5, 5);

      component.increaseQty(item);

      expect(item.quantity).toBe(5);
      expect(cartService.updateCartItem).not.toHaveBeenCalled();
      expect(component.toastTitle).toBe('Stock limit reached');
      expect(component.showToast()).toBe(true);
    });

    it('should show an error when updating quantity fails', () => {
      const item = makeItem(1, 5);
      cartService.updateCartItem.mockReturnValue(throwError(() => new Error('Update failed')));

      component.increaseQty(item);

      expect(component.showToast()).toBe(true);
      expect(component.toastTitle).toBe('Error');
      expect(component.toastMessage).toBe('Failed to update product.');
    });
  });

  describe('removeItem', () => {
    it('should remove an item and show success toast', () => {
      const remainingItems = [makeItem()];
      cartService.removeItemFromCart.mockReturnValue(
        of({ id: 1, userId: 1, items: remainingItems } as Cart),
      );

      component.removeItem(2);

      expect(cartService.removeItemFromCart).toHaveBeenCalledWith(2);
      expect(component.cartItems()).toEqual(remainingItems);
      expect(component.toastTitle).toBe('Success');
      expect(component.showToast()).toBe(true);
      expect(cartService.getMyCart).toHaveBeenCalled();
    });

    it('should show an error when removing an item fails', () => {
      cartService.removeItemFromCart.mockReturnValue(throwError(() => new Error('Remove failed')));

      component.removeItem(2);

      expect(component.toastTitle).toBe('Error');
      expect(component.toastMessage).toBe('Failed to delete product.');
      expect(component.showToast()).toBe(true);
    });
  });

  describe('getCartSubtotal', () => {
    it('should calculate the subtotal', () => {
      component.cartItems.set([
        { ...makeItem(2), price: 15 },
        { ...makeItem(3), productId: 2, price: 10 },
      ]);

      expect(component.getCartSubtotal()).toBe(60);
    });

    it('should return zero when the cart is empty', () => {
      component.cartItems.set([]);

      expect(component.getCartSubtotal()).toBe(0);
    });
  });

  describe('clearCart', () => {
    it('should clear cart items and show success toast', () => {
      component.cartItems.set([makeItem()]);

      component.clearCart();

      expect(cartService.clearCart).toHaveBeenCalled();
      expect(component.cartItems()).toEqual([]);
      expect(component.toastTitle).toBe('Success');
      expect(component.showToast()).toBe(true);
    });

    it('should show an error when clearing the cart fails', () => {
      cartService.clearCart.mockReturnValue(throwError(() => new Error('Clear failed')));

      component.clearCart();

      expect(component.toastTitle).toBe('Error');
      expect(component.toastMessage).toBe('Failed to clear cart.');
      expect(component.showToast()).toBe(true);
    });
  });

  describe('checkout', () => {
    it('should create an order and start payment', () => {
      component.cartItems.set([makeItem(2)]);

      const originalLocation = window.location;
      const locationSpy = vi.spyOn(window, 'location', 'get');

      // Avoid navigating away from the test page.
      locationSpy.mockReturnValue({
        ...originalLocation,
        set href(_url: string) {},
        get href() {
          return '';
        },
      } as Location);

      component.checkout();

      expect(orderService.createOrder).toHaveBeenCalledWith([{ productId: 1, quantity: 2 }]);
      expect(cartService.createPayment).toHaveBeenCalledWith(10);

      locationSpy.mockRestore();
    });
  });

  describe('getImageUrl', () => {
    it('should return the image URL from the service', () => {
      expect(component.getImageUrl('uploads/hoodie.jpg')).toBe(
        'http://localhost:8080/uploads/hoodie.jpg',
      );
      expect(imageUrlService.getImageUrl).toHaveBeenCalledWith('uploads/hoodie.jpg');
    });
  });

  describe('onPageShow', () => {
    it('should reload the cart when restored from browser history', () => {
      const event = { persisted: true } as PageTransitionEvent;

      component.onPageShow(event);

      expect(cartService.getMyCart).toHaveBeenCalled();
    });

    it('should not reload the cart when not restored from browser history', () => {
      const event = { persisted: false } as PageTransitionEvent;

      component.onPageShow(event);

      expect(cartService.getMyCart).not.toHaveBeenCalled();
    });
  });
});
