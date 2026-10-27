import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CartComponent } from './cart';

describe('Cart', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate the cart subtotal', () => {
    component.cartItems.set([
      {
        productId: 1,
        productName: 'Dog Hoodie',
        price: 15,
        quantity: 2,
        subtotal: 30,
        stock: 5,
        image: '',
      },
      {
        productId: 2,
        productName: 'Dog Food',
        price: 10,
        quantity: 3,
        subtotal: 30,
        stock: 8,
        image: '',
      },
    ]);

    expect(component.getCartSubtotal()).toBe(60);
  });
});
