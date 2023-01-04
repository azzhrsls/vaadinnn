package com.example.application.views.formulir;

import com.example.application.data.entity.Booking;
import com.example.application.data.service.BookingService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Formulir")
@Route(value = "formulir/:bookingID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class FormulirView extends Div implements BeforeEnterObserver {

    private final String BOOKING_ID = "bookingID";
    private final String BOOKING_EDIT_ROUTE_TEMPLATE = "formulir/%s/edit";

    private final Grid<Booking> grid = new Grid<>(Booking.class, false);

    private TextField namaPenyewa;
    private TextField noHp;
    private TextField email;
    private DatePicker bookingUntuk;
    private TextField jam;
    private TextField paket;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Booking> binder;

    private Booking booking;

    private final BookingService bookingService;

    public FormulirView(BookingService bookingService) {
        this.bookingService = bookingService;
        addClassNames("formulir-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("namaPenyewa").setAutoWidth(true);
        grid.addColumn("noHp").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("bookingUntuk").setAutoWidth(true);
        grid.addColumn("jam").setAutoWidth(true);
        grid.addColumn("paket").setAutoWidth(true);
        grid.setItems(query -> bookingService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(BOOKING_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FormulirView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Booking.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.booking == null) {
                    this.booking = new Booking();
                }
                binder.writeBean(this.booking);
                bookingService.update(this.booking);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(FormulirView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> bookingId = event.getRouteParameters().get(BOOKING_ID).map(Long::parseLong);
        if (bookingId.isPresent()) {
            Optional<Booking> bookingFromBackend = bookingService.get(bookingId.get());
            if (bookingFromBackend.isPresent()) {
                populateForm(bookingFromBackend.get());
            } else {
                Notification.show(String.format("The requested booking was not found, ID = %s", bookingId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FormulirView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        namaPenyewa = new TextField("Nama Penyewa");
        noHp = new TextField("No Hp");
        email = new TextField("Email");
        bookingUntuk = new DatePicker("Booking Untuk");
        jam = new TextField("Jam");
        paket = new TextField("Paket");
        formLayout.add(namaPenyewa, noHp, email, bookingUntuk, jam, paket);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Booking value) {
        this.booking = value;
        binder.readBean(this.booking);

    }
}
