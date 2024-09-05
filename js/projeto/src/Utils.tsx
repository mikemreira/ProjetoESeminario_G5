import React, {ChangeEvent} from "react";
import {useMaterialReactTable} from "material-react-table";
import {styled} from "@mui/material/styles";
import {DateObject} from "./obra/ObrasInfo";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import KeyboardDoubleArrowLeftIcon from "@mui/icons-material/KeyboardDoubleArrowLeft";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import Button from "@mui/material/Button";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import KeyboardDoubleArrowRightIcon from "@mui/icons-material/KeyboardDoubleArrowRight";
import TextField from "@mui/material/TextField";

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

export const formatDate = (dateString: string) => {
    if (!dateString) return null
    const date = new Date(dateString)
    const day = String(date.getDate()).padStart(2, '0')
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const year = date.getFullYear()
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`
}

export const parseDate = (dateObj: DateObject): string => {
    const day = dateObj.dayOfMonth.toString().padStart(2, '0')
    const month = dateObj.monthNumber.toString().padStart(2, '0')
    const year = dateObj.year.toString();
    return `${day}/${month}/${year}`
}


export const mapStatusToPortuguese = (status: string) => {
    const statusMap = {
        pending: "Pendente",
        completed: "Completo",
        unfinished: "Incompleto",
        unfinished_nfc: "Incompleto via NFC",
    }
    // @ts-ignore
    return statusMap[status] || status
}

interface PaginationProps {
    totalPages: number
    page: number
    handleFirstPage: () => void
    handlePreviousPage: () => void
    handlePageChange: (page: number) => void
    handleNextPage: () => void
    handleLastPage: () => void
}

export const Pagination: React.FC<PaginationProps> = ({
  totalPages,
  page,
  handleFirstPage,
  handlePreviousPage,
  handlePageChange,
  handleNextPage,
  handleLastPage,
}) => {
    return (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>
            {totalPages > 0 && (
                <>
                    {totalPages > 2 && (
                        <IconButton onClick={handleFirstPage} disabled={page === 1} title={"Retroceder tudo"}>
                            <KeyboardDoubleArrowLeftIcon />
                        </IconButton>
                    )}
                    <IconButton onClick={handlePreviousPage} disabled={page === 1} title={"Retroceder"}>
                        <ArrowBackIosIcon />
                    </IconButton>
                    {[...Array(3)].map((_, index) => {
                        const startPage = Math.max(1, Math.min(page - 1, totalPages - 2));
                        const currentPage = startPage + index;
                        if (currentPage <= totalPages) {
                            return (
                                <Button
                                    key={currentPage}
                                    onClick={() => handlePageChange(currentPage)}
                                    variant={page === currentPage ? "contained" : "outlined"}
                                    sx={{
                                        minWidth: "40px",
                                        minHeight: "40px",
                                        borderRadius: "50%",
                                        mx: 1,
                                    }}
                                >
                                    {currentPage}
                                </Button>
                            );
                        }
                        return null;
                    })}
                    <IconButton onClick={handleNextPage} disabled={page === totalPages} title={"Avançar"}>
                        <ArrowForwardIosIcon />
                    </IconButton>
                    {totalPages > 2 && (
                        <IconButton onClick={handleLastPage} disabled={page === totalPages} title={"Avançar tudo"}>
                            <KeyboardDoubleArrowRightIcon />
                        </IconButton>
                    )}
                </>
            )}
        </Box>
    )
}

interface DateRangeFilterProps {
    initialDate: string | null
    endDate: string | null
    setInitialDate: (date: string) => void
    setEndDate: (date: string) => void
    handleFilterReset: () => void
}

export const DateRangeFilter: React.FC<DateRangeFilterProps> = ({
    initialDate,
    endDate,
    setInitialDate,
    setEndDate,
    handleFilterReset,
}) => {
    return (
        <Box sx={{ display: "flex", alignItems: "center" }}>
            <TextField
                label="Desde"
                type="date"
                value={initialDate || ""}
                onChange={(e) => setInitialDate(e.target.value)}
                InputLabelProps={{ shrink: true }}
                sx={{ marginRight: 2 }}
            />
            <TextField
                label="Até"
                type="date"
                value={endDate || ""}
                onChange={(e) => setEndDate(e.target.value)}
                InputLabelProps={{ shrink: true }}
                sx={{ marginRight: 2 }}
            />
            <Button variant="contained" color="primary" onClick={handleFilterReset}>
                Limpar Pesquisa
            </Button>
        </Box>
    )
}

