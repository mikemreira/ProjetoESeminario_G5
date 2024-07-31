import React, {ChangeEvent} from "react";
import {useMaterialReactTable} from "material-react-table";
import {styled} from "@mui/material/styles";

export const table = (columns: any, data: any) => useMaterialReactTable({
    columns,
    data: data,
    enableRowSelection: false,
    initialState: {
        pagination: { pageSize: 5, pageIndex: 0 },
        showGlobalFilter: true,
    },
    muiPaginationProps: {
        rowsPerPageOptions: [5, 10, 15],
        variant: 'outlined',
    },
    paginationDisplayMode: 'pages',
});

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

export const handleChange = ( setEditedUser: (arg0: (prevUser: any) => any) => void) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target
    setEditedUser((prevUser) => ({
        ...prevUser!,
        [name]: value,
    }))
}

export const handleFileChange = ( setValues: (arg0: (values: any) => any) => void) => (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
            setValues((values) => ({
                ...values!,
                foto: reader.result as string| null,
            }));
        };
        reader.readAsDataURL(file);
    }
};

export const handleSelectChange = ( setValues: (arg0: (values: any) => any) => void) => (event: ChangeEvent<HTMLInputElement>) => {
    setValues((values) => ({
        ...values,
        function: event.target.value as string
    }));
};

export const handleRoleChange = (setValues: (arg0: (values: any) => any) => void) => (event: { target: { value: string; }; }) => {
    setValues((values) => ({
        ...values,
        role: event.target.value as string
    }))
}

export const handleSelectObraChange = (setEditedObra: (arg0: (prevObra: any) => any) => void) => (event: ChangeEvent<{ name?: string; value: unknown }>) => {
    const { name, value } = event.target
    setEditedObra((prevObra) => ({
        ...prevObra!,
        [name as string]: value,
    }))
}

export const VisuallyHiddenInput = styled('input')({
    clip: 'rect(0 0 0 0)',
    clipPath: 'inset(50%)',
    height: 1,
    overflow: 'hidden',
    position: 'absolute',
    bottom: 0,
    left: 0,
    whiteSpace: 'nowrap',
    width: 1,
});
