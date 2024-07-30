import React from "react";

export const handleInputChange = <T extends Record<string, any>>(
    setValues: React.Dispatch<React.SetStateAction<T>>
) => (
    event: React.ChangeEvent<HTMLInputElement>
) => {
    event.preventDefault();
    const { name, value } = event.target;
    setValues((values) => ({
        ...values,
        [name]: value
    }));
};