import { Component, signal } from '@angular/core';
import { User } from '../../../../models/user';
import { UserService } from '../../../../services/user.service';
import { FormsModule } from '@angular/forms';
import { Toast } from '../../../toast/toast';

@Component({
  selector: 'app-admin-users',
  imports: [FormsModule, Toast],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers {
  constructor(private userService: UserService) {}

  users = signal<User[]>([]);
  isEditOpen = signal(false);
  selectedUser = signal<User | null>(null);

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  openEdit(user: User) {
    this.selectedUser.set({ ...user });
    this.isEditOpen.set(true);
  }

  closeEdit() {
    this.isEditOpen.set(false);
    this.selectedUser.set(null);
  }

  getUsers() {
    this.userService.getAllUsers().subscribe((res) => {
      this.users.set(res);
    });
  }

  ngOnInit() {
    this.getUsers();
  }

  updateUser(id: number, data: User) {
    if (
      (data.role === 'ADMIN' || data.role === 'USER') &&
      data.role !== this.users().find((user) => user.id === id)?.role &&
      !confirm(`Are you sure you want to change this user's role to ${data.role}?`)
    ) {
      return;
    }

    this.userService.updateUser(id, data).subscribe({
      next: (res) => {
        this.users.update((users) => users.map((user) => (user.id === id ? res : user)));

        this.showToast.set(true);
        this.toastTitle = 'Success';
        this.toastMessage = 'Email updated successfully';
        this.toastType = 'success';

        this.closeEdit();
      },
      error: (err) => {
        this.showToast.set(true);
        this.toastTitle = 'Error';
        this.toastMessage = err.error;
        this.toastType = 'error';
      },
    });
  }
}
