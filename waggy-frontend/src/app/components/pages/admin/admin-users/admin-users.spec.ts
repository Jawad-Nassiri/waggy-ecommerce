import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminUsers } from './admin-users';
import { UserService } from '../../../../services/user.service';
import { of } from 'rxjs';

describe('AdminUsers', () => {
  let component: AdminUsers;
  let fixture: ComponentFixture<AdminUsers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminUsers],
      providers: [
        {
          provide: UserService,
          useValue: {
            getAllUsers: () => of([]),
            updateUser: () => of({}),
            deleteUser: () => of({}),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsers);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});