import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { Login } from './login';
import { of, throwError } from 'rxjs';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;

  const authServiceMock = {
    loadCurrentUser: vi.fn(),
    login: vi.fn(),
  };

  const routerMock = {
    navigate: vi.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    })
      .overrideComponent(Login, {
        set: { template: '' },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate email', () => {
    const input = document.createElement('input');

    component.validateEmail('jawad@mail.com', input);

    expect(component.emailValid).toBe(true);
  });

  it('should invalidate email', () => {
    const input = document.createElement('input');

    component.validateEmail('wrong-email', input);

    expect(component.emailValid).toBe(false);
  });

  it('should validate password', () => {
    const input = document.createElement('input');

    component.validatePassword('Password1!', input);

    expect(component.passwordValid).toBe(true);
  });

  it('should invalidate password', () => {
    const input = document.createElement('input');

    component.validatePassword('123', input);

    expect(component.passwordValid).toBe(false);
  });

  it('should login successfully', () => {
    authServiceMock.login.mockReturnValue(of({}));

    component.emailValid = true;
    component.passwordValid = true;

    const emailInput = document.createElement('input');
    const passwordInput = document.createElement('input');

    emailInput.value = 'jawad@mail.com';
    passwordInput.value = 'Password1!';

    component.login(emailInput, passwordInput);

    expect(authServiceMock.login).toHaveBeenCalledWith('jawad@mail.com', 'Password1!');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/'], {
      state: { toastMessage: 'Login successful!' },
    });
  });

  it('should show error when login fails', () => {
    authServiceMock.login.mockReturnValue(throwError(() => ({ error: 'Wrong password' })));

    component.emailValid = true;
    component.passwordValid = true;

    const emailInput = document.createElement('input');
    const passwordInput = document.createElement('input');

    emailInput.value = 'jawad@mail.com';
    passwordInput.value = 'WrongPassword1!';

    component.login(emailInput, passwordInput);

    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Wrong password');
    expect(component.toastType).toBe('error');
    expect(component.showToast()).toBe(true);
  });
});
