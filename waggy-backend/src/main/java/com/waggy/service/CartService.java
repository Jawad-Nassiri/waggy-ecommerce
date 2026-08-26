package com.waggy.service;

import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.entity.Cart;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.exception.CartNotFoundException;
import com.waggy.exception.UnauthorizedException;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.CartMapper;
import com.waggy.repository.CartRepository;
import com.waggy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;

    public CartResponseDTO addCartInDb() {
        // get the email of the currently logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        Cart cart = new Cart();
        cart.setUser(user);

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDTO(savedCart);
    }


    public List<CartResponseDTO> findAllCarts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        if (user.getRole().equals(Role.ADMIN)) {
            return cartRepository.findAll().stream().map(cartMapper::toDTO).toList();
        } else {
            return cartRepository.findByUserId(user.getId())
                    .map(cartMapper::toDTO)
                    .map(List::of)
                    .orElse(List.of());
        }
    }

    public CartResponseDTO findCartById(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart not found !"));

        if (!user.getRole().equals(Role.ADMIN)
                && !cart.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot access this cart !");
        }

        return cartMapper.toDTO(cart);
    }


    public void deleteCart(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart not found !"));

        if (!user.getRole().equals(Role.ADMIN)
                && !cart.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot delete this cart !");
        }

        cartRepository.delete(cart);
    }


}
