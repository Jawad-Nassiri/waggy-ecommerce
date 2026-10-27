import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Signup } from './signup';
import { AuthService } from '../../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('Signup', () => {
  let component: Signup;
  let fixture: ComponentFixture<Signup>;

  const authServiceMock = {
    loadCurrentUser: vi.fn().mockReturnValue(of(null)),
    signup: vi.fn(),
  };

  const routerMock = {
    navigate: vi.fn(),
  };

  beforeEach(async () => {
    authServiceMock.signup.mockReset();
    authServiceMock.loadCurrentUser.mockReturnValue(of(null));
    routerMock.navigate.mockReset();

    await TestBed.configureTestingModule({
      imports: [Signup],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    })
      .overrideComponent(Signup, {
        set: { template: '' },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Signup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate name', () => {
    const input = document.createElement('input');

    component.validateName('Jo', input);
    expect(component.nameValid).toBe(false);

    component.validateName('Jawad', input);
    expect(component.nameValid).toBe(true);
  });

  it('should validate email', () => {
    const input = document.createElement('input');

    component.validateEmail('jawad@mail', input);
    expect(component.emailValid).toBe(false);

    component.validateEmail('jawad@mail.com', input);
    expect(component.emailValid).toBe(true);
  });

  it('should validate password', () => {
    const input = document.createElement('input');
    const confirmInput = document.createElement('input');

    component.validatePassword(
      'abc',
      { currentTarget: input } as unknown as Event,
      confirmInput,
    );
    expect(component.passwordValid).toBe(false);

    component.validatePassword(
      'Abc123!',
      { currentTarget: input } as unknown as Event,
      confirmInput,
    );
    expect(component.passwordValid).toBe(true);
  });

  it('should validate confirm password', () => {
    const input = document.createElement('input');

    input.value = 'wrong';
    component.validateConfirmPassword('Abc123!', input);
    expect(component.confirmPasswordValid).toBe(false);

    input.value = 'Abc123!';
    component.validateConfirmPassword('Abc123!', input);
    expect(component.confirmPasswordValid).toBe(true);
  });

  it('should signup successfully', () => {
    authServiceMock.signup.mockReturnValue(of({}));

    const nameInput = document.createElement('input');
    const emailInput = document.createElement('input');
    const passwordInput = document.createElement('input');
    const confirmPasswordInput = document.createElement('input');

    nameInput.value = 'Jawad';
    emailInput.value = 'jawad@mail.com';
    passwordInput.value = 'Abc123!';
    confirmPasswordInput.value = 'Abc123!';

    component.nameValid = true;
    component.emailValid = true;
    component.passwordValid = true;
    component.confirmPasswordValid = true;

    component.signup(nameInput, emailInput, passwordInput, confirmPasswordInput);

    expect(authServiceMock.signup).toHaveBeenCalledWith(
      'Jawad',
      'jawad@mail.com',
      'Abc123!',
    );
    expect(authServiceMock.loadCurrentUser).toHaveBeenCalled();
  });

  it('should show error toast when signup fails', () => {
    authServiceMock.signup.mockReturnValue(
      throwError(() => ({ error: 'Email already exists' })),
    );

    const nameInput = document.createElement('input');
    const emailInput = document.createElement('input');
    const passwordInput = document.createElement('input');
    const confirmPasswordInput = document.createElement('input');

    nameInput.value = 'Jawad';
    emailInput.value = 'jawad@mail.com';
    passwordInput.value = 'Abc123!';
    confirmPasswordInput.value = 'Abc123!';

    component.nameValid = true;
    component.emailValid = true;
    component.passwordValid = true;
    component.confirmPasswordValid = true;

    component.signup(nameInput, emailInput, passwordInput, confirmPasswordInput);

    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Email already exists');
    expect(component.toastType).toBe('error');
    expect(component.showToast()).toBe(true);
  });
});