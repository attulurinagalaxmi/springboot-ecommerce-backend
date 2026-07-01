package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.OrderItemRequestDTO;
import com.example.ecommerce.dto.OrderItemResponseDTO;
import com.example.ecommerce.dto.OrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderItemMapper;
import com.example.ecommerce.model.Audit;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderItemMapper orderItemMapper;
	//private final OrderMapper orderMapper;

	@Audit(action = "CREATE_ORDER")
	public OrderResponseDTO placeOrder(OrderRequestDTO request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Order order = new Order();

		order.setUser(user);

		order.setTotalAmount(0.0);
		List<OrderItem> orderItems = new ArrayList<>();
		List<OrderItemResponseDTO> orderItemsResDTOs = new ArrayList<>();

		double totalAmount = 0;
		for (OrderItemRequestDTO itemDTO : request.getItems()) {
			Product product = productRepository.findById(itemDTO.getProductId())
					.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
			if (product.getStockQuantity() < itemDTO.getQuantity()) {

				throw new RuntimeException("Insufficient stock");
			}
			double subtotal = product.getPrice() * itemDTO.getQuantity();
			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);

			orderItem.setProduct(product);

			orderItem.setQuantity(itemDTO.getQuantity());

			orderItem.setPrice(product.getPrice());
			product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
			totalAmount += subtotal;
			orderItems.add(orderItem);
			OrderItemResponseDTO orderItemResDTO = orderItemMapper.toDTO(orderItem);
			orderItemResDTO.setProductName(product.getName());
			orderItemResDTO.setSubtotal(subtotal);
			orderItemsResDTOs.add(orderItemResDTO);
		}
		order.setOrderItems(orderItems);

		order.setTotalAmount(totalAmount);
		Order savedOrder = orderRepository.save(order);
		
		OrderResponseDTO resDTO = new OrderResponseDTO();
		resDTO.setItems(orderItemsResDTOs);
		resDTO.setOrderId(savedOrder.getId());
		resDTO.setTotalAmount(totalAmount);
		resDTO.setUserEmail(user.getEmail());
		return resDTO;
		
	}
	public List<OrderResponseDTO> getUserOrders() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		List<Order> orders = orderRepository.findOrdersWithItemsAndProducts(email);
		return convertOrderListToOrderListDTO(email, orders);
	}
	private List<OrderResponseDTO> convertOrderListToOrderListDTO(String email, List<Order> orders) {
		List<OrderResponseDTO> orderList = new ArrayList<>();
		for (Order order: orders) {
			orderList.add(convertOrderToOrderDTO(email, order));
		}
		return orderList;
	}
	public OrderResponseDTO getOrderById(Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

//		this query is for fetch join to avoid N+1 problem caused by Lazy intialization of Order entity relationsships so join fetch is best to load in one query instread of lazy loading its relations everytime on a loop. 
		//Order order = orderRepository.findOrdersWithItemsByIdAndUserEmail(id,email).orElseThrow(() -> new UserNotFoundException("Order not found"));
		//this is the cleaner alternative for fetch join whoch we mannually mentioned in query .EntityGraph automatically fetches all its relations in one go without even metioning it 
		//we use @ annotation and mention attributeParams -- what relationships you want to load and that it cleaner code 
		Order order = orderRepository.findByIdAndUserEmail(id,email).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
		return convertOrderToOrderDTO(email, order);
	}
	private OrderResponseDTO convertOrderToOrderDTO(String email, Order order) {
		List<OrderItemResponseDTO> orderItemsResDTOs = new ArrayList<>();
		for (OrderItem orderItems : order.getOrderItems()) {
			OrderItemResponseDTO orderItemDTO = orderItemMapper.toDTO(orderItems);
			orderItemsResDTOs.add(orderItemDTO);
		}
		OrderResponseDTO resDTO = new OrderResponseDTO();
		resDTO.setItems(orderItemsResDTOs);
		resDTO.setOrderId(order.getId());
		resDTO.setTotalAmount(order.getTotalAmount());
		resDTO.setUserEmail(email);
		return resDTO;
	}
	public List<OrderResponseDTO> getAllOrders() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return convertOrderListToOrderListDTO(email, orderRepository.findAll());
	}
}
