import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminOrders } from './admin-orders';
import { OrderService } from '../../../../services/order.service';
import { ImageUrlService } from '../../../../services/image-url.service';
import { Order } from '../../../../models/order';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('AdminOrders', () => {
  let component: AdminOrders;
  let fixture: ComponentFixture<AdminOrders>;

  let orderService: {
    getAllOrders: ReturnType<typeof vi.fn>;
    deleteOrder: ReturnType<typeof vi.fn>;
  };

  let imageUrlService: {
    getImageUrl: ReturnType<typeof vi.fn>;
  };

  const mockOrders = [
    {
      id: 1,
      orderDate: '2026-10-20',
      items: [],
      totalPrice: 30,
      status: 'PENDING',
    },
    {
      id: 2,
      orderDate: '2026-10-21',
      items: [],
      totalPrice: 50,
      status: 'PAID',
    },
  ] as Order[];

  beforeEach(async () => {
    orderService = {
      getAllOrders: vi.fn(() => of(mockOrders)),
      deleteOrder: vi.fn(() => of({})),
    };

    imageUrlService = {
      getImageUrl: vi.fn((image: string) => `http://localhost:8080/${image}`),
    };

    await TestBed.configureTestingModule({
      imports: [AdminOrders],
      providers: [
        { provide: OrderService, useValue: orderService },
        { provide: ImageUrlService, useValue: imageUrlService },
      ],
    })
      .overrideComponent(AdminOrders, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(AdminOrders);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load orders on initialization', () => {
    component.ngOnInit();

    expect(orderService.getAllOrders).toHaveBeenCalled();
    expect(component.orders()).toEqual(mockOrders);
  });

  it('should load all orders successfully', () => {
    component.getAllOrders();

    expect(component.orders()).toEqual(mockOrders);
  });

  it('should handle an error when loading orders', () => {
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
    orderService.getAllOrders.mockReturnValue(
      throwError(() => ({ error: 'Load failed' })),
    );

    component.getAllOrders();

    expect(consoleError).toHaveBeenCalledWith(
      'Failed to load orders',
      'Load failed',
    );

    consoleError.mockRestore();
  });

  it('should return an image URL from the image service', () => {
    expect(component.getImageUrl('hoodie.jpg')).toBe(
      'http://localhost:8080/hoodie.jpg',
    );
    expect(imageUrlService.getImageUrl).toHaveBeenCalledWith('hoodie.jpg');
  });

  it('should open order details', () => {
    component.openDetails(mockOrders[0]);

    expect(component.selectedOrder()).toEqual(mockOrders[0]);
    expect(component.isDetailsOpen()).toBe(true);
  });

  it('should close order details and clear the selected order', () => {
    component.openDetails(mockOrders[0]);

    component.closeDetails();

    expect(component.isDetailsOpen()).toBe(false);
    expect(component.selectedOrder()).toBeNull();
  });

  it('should ask for confirmation before deleting an order', () => {
    component.deleteOrder(1);

    expect(component.showConfirm()).toBe(true);
    expect(component.confirmMessage).toBe(
      'Are you sure you want to delete this order?',
    );
    expect(orderService.deleteOrder).not.toHaveBeenCalled();
  });

  it('should delete an order after confirmation', () => {
    component.orders.set(mockOrders);

    component.deleteOrder(1);
    component.onConfirmYes();

    expect(orderService.deleteOrder).toHaveBeenCalledWith(1);
    expect(component.orders()).toEqual([mockOrders[1]]);
    expect(component.showConfirm()).toBe(false);
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('Order deleted successfully!');
    expect(component.toastType).toBe('success');
  });

  it('should show an error toast when deleting an order fails', () => {
    orderService.deleteOrder.mockReturnValue(
      throwError(() => new Error('Delete failed')),
    );

    component.deleteOrder(1);
    component.onConfirmYes();

    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Failed to delete order!');
    expect(component.toastType).toBe('error');
  });
});