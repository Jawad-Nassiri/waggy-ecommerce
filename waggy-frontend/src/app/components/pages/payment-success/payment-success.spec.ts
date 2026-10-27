import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { PaymentSuccess } from './payment-success';
import { of } from 'rxjs';

describe('PaymentSuccess', () => {
  let component: PaymentSuccess;
  let fixture: ComponentFixture<PaymentSuccess>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentSuccess],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: () => '1',
              },
            },
          },
        },
        {
          provide: OrderService,
          useValue: {
            getOrders: () => of([{ id: 1, status: 'PAID' }]),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentSuccess);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});